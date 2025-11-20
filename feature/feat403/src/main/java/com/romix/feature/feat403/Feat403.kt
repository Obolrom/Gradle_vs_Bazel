package com.romix.feature.feat403

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat403Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat403UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat403FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat403UserSummary
)

data class Feat403UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat403NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat403Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat403Config = Feat403Config()
) {

    fun loadSnapshot(userId: Long): Feat403NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat403NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat403UserSummary {
        return Feat403UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat403FeedItem> {
        val result = java.util.ArrayList<Feat403FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat403FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat403UiMapper {

    fun mapToUi(model: List<Feat403FeedItem>): Feat403UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat403UiModel(
            header = UiText("Feat403 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat403UiModel =
        Feat403UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat403UiModel =
        Feat403UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat403UiModel =
        Feat403UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat403Service(
    private val repository: Feat403Repository,
    private val uiMapper: Feat403UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat403UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat403UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat403UserItem1(val user: CoreUser, val label: String)
data class Feat403UserItem2(val user: CoreUser, val label: String)
data class Feat403UserItem3(val user: CoreUser, val label: String)
data class Feat403UserItem4(val user: CoreUser, val label: String)
data class Feat403UserItem5(val user: CoreUser, val label: String)
data class Feat403UserItem6(val user: CoreUser, val label: String)
data class Feat403UserItem7(val user: CoreUser, val label: String)
data class Feat403UserItem8(val user: CoreUser, val label: String)
data class Feat403UserItem9(val user: CoreUser, val label: String)
data class Feat403UserItem10(val user: CoreUser, val label: String)

data class Feat403StateBlock1(val state: Feat403UiModel, val checksum: Int)
data class Feat403StateBlock2(val state: Feat403UiModel, val checksum: Int)
data class Feat403StateBlock3(val state: Feat403UiModel, val checksum: Int)
data class Feat403StateBlock4(val state: Feat403UiModel, val checksum: Int)
data class Feat403StateBlock5(val state: Feat403UiModel, val checksum: Int)
data class Feat403StateBlock6(val state: Feat403UiModel, val checksum: Int)
data class Feat403StateBlock7(val state: Feat403UiModel, val checksum: Int)
data class Feat403StateBlock8(val state: Feat403UiModel, val checksum: Int)
data class Feat403StateBlock9(val state: Feat403UiModel, val checksum: Int)
data class Feat403StateBlock10(val state: Feat403UiModel, val checksum: Int)

fun buildFeat403UserItem(user: CoreUser, index: Int): Feat403UserItem1 {
    return Feat403UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat403StateBlock(model: Feat403UiModel): Feat403StateBlock1 {
    return Feat403StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat403UserSummary> {
    val list = java.util.ArrayList<Feat403UserSummary>(users.size)
    for (user in users) {
        list += Feat403UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat403UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat403UiModel {
    val summaries = (0 until count).map {
        Feat403UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat403UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat403UiModel> {
    val models = java.util.ArrayList<Feat403UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat403AnalyticsEvent1(val name: String, val value: String)
data class Feat403AnalyticsEvent2(val name: String, val value: String)
data class Feat403AnalyticsEvent3(val name: String, val value: String)
data class Feat403AnalyticsEvent4(val name: String, val value: String)
data class Feat403AnalyticsEvent5(val name: String, val value: String)
data class Feat403AnalyticsEvent6(val name: String, val value: String)
data class Feat403AnalyticsEvent7(val name: String, val value: String)
data class Feat403AnalyticsEvent8(val name: String, val value: String)
data class Feat403AnalyticsEvent9(val name: String, val value: String)
data class Feat403AnalyticsEvent10(val name: String, val value: String)

fun logFeat403Event1(event: Feat403AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat403Event2(event: Feat403AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat403Event3(event: Feat403AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat403Event4(event: Feat403AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat403Event5(event: Feat403AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat403Event6(event: Feat403AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat403Event7(event: Feat403AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat403Event8(event: Feat403AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat403Event9(event: Feat403AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat403Event10(event: Feat403AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat403Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat403Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat403Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat403Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat403Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat403Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat403Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat403Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat403Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat403Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat403(u: CoreUser): Feat403Projection1 =
    Feat403Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat403Projection1> {
    val list = java.util.ArrayList<Feat403Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat403(u)
    }
    return list
}
