package com.romix.feature.feat103

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat103Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat103UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat103FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat103UserSummary
)

data class Feat103UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat103NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat103Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat103Config = Feat103Config()
) {

    fun loadSnapshot(userId: Long): Feat103NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat103NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat103UserSummary {
        return Feat103UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat103FeedItem> {
        val result = java.util.ArrayList<Feat103FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat103FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat103UiMapper {

    fun mapToUi(model: List<Feat103FeedItem>): Feat103UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat103UiModel(
            header = UiText("Feat103 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat103UiModel =
        Feat103UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat103UiModel =
        Feat103UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat103UiModel =
        Feat103UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat103Service(
    private val repository: Feat103Repository,
    private val uiMapper: Feat103UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat103UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat103UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat103UserItem1(val user: CoreUser, val label: String)
data class Feat103UserItem2(val user: CoreUser, val label: String)
data class Feat103UserItem3(val user: CoreUser, val label: String)
data class Feat103UserItem4(val user: CoreUser, val label: String)
data class Feat103UserItem5(val user: CoreUser, val label: String)
data class Feat103UserItem6(val user: CoreUser, val label: String)
data class Feat103UserItem7(val user: CoreUser, val label: String)
data class Feat103UserItem8(val user: CoreUser, val label: String)
data class Feat103UserItem9(val user: CoreUser, val label: String)
data class Feat103UserItem10(val user: CoreUser, val label: String)

data class Feat103StateBlock1(val state: Feat103UiModel, val checksum: Int)
data class Feat103StateBlock2(val state: Feat103UiModel, val checksum: Int)
data class Feat103StateBlock3(val state: Feat103UiModel, val checksum: Int)
data class Feat103StateBlock4(val state: Feat103UiModel, val checksum: Int)
data class Feat103StateBlock5(val state: Feat103UiModel, val checksum: Int)
data class Feat103StateBlock6(val state: Feat103UiModel, val checksum: Int)
data class Feat103StateBlock7(val state: Feat103UiModel, val checksum: Int)
data class Feat103StateBlock8(val state: Feat103UiModel, val checksum: Int)
data class Feat103StateBlock9(val state: Feat103UiModel, val checksum: Int)
data class Feat103StateBlock10(val state: Feat103UiModel, val checksum: Int)

fun buildFeat103UserItem(user: CoreUser, index: Int): Feat103UserItem1 {
    return Feat103UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat103StateBlock(model: Feat103UiModel): Feat103StateBlock1 {
    return Feat103StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat103UserSummary> {
    val list = java.util.ArrayList<Feat103UserSummary>(users.size)
    for (user in users) {
        list += Feat103UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat103UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat103UiModel {
    val summaries = (0 until count).map {
        Feat103UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat103UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat103UiModel> {
    val models = java.util.ArrayList<Feat103UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat103AnalyticsEvent1(val name: String, val value: String)
data class Feat103AnalyticsEvent2(val name: String, val value: String)
data class Feat103AnalyticsEvent3(val name: String, val value: String)
data class Feat103AnalyticsEvent4(val name: String, val value: String)
data class Feat103AnalyticsEvent5(val name: String, val value: String)
data class Feat103AnalyticsEvent6(val name: String, val value: String)
data class Feat103AnalyticsEvent7(val name: String, val value: String)
data class Feat103AnalyticsEvent8(val name: String, val value: String)
data class Feat103AnalyticsEvent9(val name: String, val value: String)
data class Feat103AnalyticsEvent10(val name: String, val value: String)

fun logFeat103Event1(event: Feat103AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat103Event2(event: Feat103AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat103Event3(event: Feat103AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat103Event4(event: Feat103AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat103Event5(event: Feat103AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat103Event6(event: Feat103AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat103Event7(event: Feat103AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat103Event8(event: Feat103AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat103Event9(event: Feat103AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat103Event10(event: Feat103AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat103Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat103Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat103Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat103Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat103Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat103Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat103Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat103Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat103Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat103Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat103(u: CoreUser): Feat103Projection1 =
    Feat103Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat103Projection1> {
    val list = java.util.ArrayList<Feat103Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat103(u)
    }
    return list
}
