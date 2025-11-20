package com.romix.feature.feat168

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat168Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat168UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat168FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat168UserSummary
)

data class Feat168UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat168NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat168Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat168Config = Feat168Config()
) {

    fun loadSnapshot(userId: Long): Feat168NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat168NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat168UserSummary {
        return Feat168UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat168FeedItem> {
        val result = java.util.ArrayList<Feat168FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat168FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat168UiMapper {

    fun mapToUi(model: List<Feat168FeedItem>): Feat168UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat168UiModel(
            header = UiText("Feat168 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat168UiModel =
        Feat168UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat168UiModel =
        Feat168UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat168UiModel =
        Feat168UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat168Service(
    private val repository: Feat168Repository,
    private val uiMapper: Feat168UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat168UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat168UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat168UserItem1(val user: CoreUser, val label: String)
data class Feat168UserItem2(val user: CoreUser, val label: String)
data class Feat168UserItem3(val user: CoreUser, val label: String)
data class Feat168UserItem4(val user: CoreUser, val label: String)
data class Feat168UserItem5(val user: CoreUser, val label: String)
data class Feat168UserItem6(val user: CoreUser, val label: String)
data class Feat168UserItem7(val user: CoreUser, val label: String)
data class Feat168UserItem8(val user: CoreUser, val label: String)
data class Feat168UserItem9(val user: CoreUser, val label: String)
data class Feat168UserItem10(val user: CoreUser, val label: String)

data class Feat168StateBlock1(val state: Feat168UiModel, val checksum: Int)
data class Feat168StateBlock2(val state: Feat168UiModel, val checksum: Int)
data class Feat168StateBlock3(val state: Feat168UiModel, val checksum: Int)
data class Feat168StateBlock4(val state: Feat168UiModel, val checksum: Int)
data class Feat168StateBlock5(val state: Feat168UiModel, val checksum: Int)
data class Feat168StateBlock6(val state: Feat168UiModel, val checksum: Int)
data class Feat168StateBlock7(val state: Feat168UiModel, val checksum: Int)
data class Feat168StateBlock8(val state: Feat168UiModel, val checksum: Int)
data class Feat168StateBlock9(val state: Feat168UiModel, val checksum: Int)
data class Feat168StateBlock10(val state: Feat168UiModel, val checksum: Int)

fun buildFeat168UserItem(user: CoreUser, index: Int): Feat168UserItem1 {
    return Feat168UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat168StateBlock(model: Feat168UiModel): Feat168StateBlock1 {
    return Feat168StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat168UserSummary> {
    val list = java.util.ArrayList<Feat168UserSummary>(users.size)
    for (user in users) {
        list += Feat168UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat168UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat168UiModel {
    val summaries = (0 until count).map {
        Feat168UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat168UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat168UiModel> {
    val models = java.util.ArrayList<Feat168UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat168AnalyticsEvent1(val name: String, val value: String)
data class Feat168AnalyticsEvent2(val name: String, val value: String)
data class Feat168AnalyticsEvent3(val name: String, val value: String)
data class Feat168AnalyticsEvent4(val name: String, val value: String)
data class Feat168AnalyticsEvent5(val name: String, val value: String)
data class Feat168AnalyticsEvent6(val name: String, val value: String)
data class Feat168AnalyticsEvent7(val name: String, val value: String)
data class Feat168AnalyticsEvent8(val name: String, val value: String)
data class Feat168AnalyticsEvent9(val name: String, val value: String)
data class Feat168AnalyticsEvent10(val name: String, val value: String)

fun logFeat168Event1(event: Feat168AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat168Event2(event: Feat168AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat168Event3(event: Feat168AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat168Event4(event: Feat168AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat168Event5(event: Feat168AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat168Event6(event: Feat168AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat168Event7(event: Feat168AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat168Event8(event: Feat168AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat168Event9(event: Feat168AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat168Event10(event: Feat168AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat168Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat168Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat168Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat168Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat168Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat168Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat168Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat168Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat168Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat168Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat168(u: CoreUser): Feat168Projection1 =
    Feat168Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat168Projection1> {
    val list = java.util.ArrayList<Feat168Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat168(u)
    }
    return list
}
