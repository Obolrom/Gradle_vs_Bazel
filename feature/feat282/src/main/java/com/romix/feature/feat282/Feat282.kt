package com.romix.feature.feat282

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat282Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat282UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat282FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat282UserSummary
)

data class Feat282UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat282NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat282Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat282Config = Feat282Config()
) {

    fun loadSnapshot(userId: Long): Feat282NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat282NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat282UserSummary {
        return Feat282UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat282FeedItem> {
        val result = java.util.ArrayList<Feat282FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat282FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat282UiMapper {

    fun mapToUi(model: List<Feat282FeedItem>): Feat282UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat282UiModel(
            header = UiText("Feat282 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat282UiModel =
        Feat282UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat282UiModel =
        Feat282UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat282UiModel =
        Feat282UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat282Service(
    private val repository: Feat282Repository,
    private val uiMapper: Feat282UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat282UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat282UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat282UserItem1(val user: CoreUser, val label: String)
data class Feat282UserItem2(val user: CoreUser, val label: String)
data class Feat282UserItem3(val user: CoreUser, val label: String)
data class Feat282UserItem4(val user: CoreUser, val label: String)
data class Feat282UserItem5(val user: CoreUser, val label: String)
data class Feat282UserItem6(val user: CoreUser, val label: String)
data class Feat282UserItem7(val user: CoreUser, val label: String)
data class Feat282UserItem8(val user: CoreUser, val label: String)
data class Feat282UserItem9(val user: CoreUser, val label: String)
data class Feat282UserItem10(val user: CoreUser, val label: String)

data class Feat282StateBlock1(val state: Feat282UiModel, val checksum: Int)
data class Feat282StateBlock2(val state: Feat282UiModel, val checksum: Int)
data class Feat282StateBlock3(val state: Feat282UiModel, val checksum: Int)
data class Feat282StateBlock4(val state: Feat282UiModel, val checksum: Int)
data class Feat282StateBlock5(val state: Feat282UiModel, val checksum: Int)
data class Feat282StateBlock6(val state: Feat282UiModel, val checksum: Int)
data class Feat282StateBlock7(val state: Feat282UiModel, val checksum: Int)
data class Feat282StateBlock8(val state: Feat282UiModel, val checksum: Int)
data class Feat282StateBlock9(val state: Feat282UiModel, val checksum: Int)
data class Feat282StateBlock10(val state: Feat282UiModel, val checksum: Int)

fun buildFeat282UserItem(user: CoreUser, index: Int): Feat282UserItem1 {
    return Feat282UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat282StateBlock(model: Feat282UiModel): Feat282StateBlock1 {
    return Feat282StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat282UserSummary> {
    val list = java.util.ArrayList<Feat282UserSummary>(users.size)
    for (user in users) {
        list += Feat282UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat282UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat282UiModel {
    val summaries = (0 until count).map {
        Feat282UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat282UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat282UiModel> {
    val models = java.util.ArrayList<Feat282UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat282AnalyticsEvent1(val name: String, val value: String)
data class Feat282AnalyticsEvent2(val name: String, val value: String)
data class Feat282AnalyticsEvent3(val name: String, val value: String)
data class Feat282AnalyticsEvent4(val name: String, val value: String)
data class Feat282AnalyticsEvent5(val name: String, val value: String)
data class Feat282AnalyticsEvent6(val name: String, val value: String)
data class Feat282AnalyticsEvent7(val name: String, val value: String)
data class Feat282AnalyticsEvent8(val name: String, val value: String)
data class Feat282AnalyticsEvent9(val name: String, val value: String)
data class Feat282AnalyticsEvent10(val name: String, val value: String)

fun logFeat282Event1(event: Feat282AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat282Event2(event: Feat282AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat282Event3(event: Feat282AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat282Event4(event: Feat282AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat282Event5(event: Feat282AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat282Event6(event: Feat282AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat282Event7(event: Feat282AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat282Event8(event: Feat282AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat282Event9(event: Feat282AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat282Event10(event: Feat282AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat282Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat282Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat282Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat282Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat282Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat282Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat282Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat282Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat282Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat282Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat282(u: CoreUser): Feat282Projection1 =
    Feat282Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat282Projection1> {
    val list = java.util.ArrayList<Feat282Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat282(u)
    }
    return list
}
