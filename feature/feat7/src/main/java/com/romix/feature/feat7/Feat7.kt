package com.romix.feature.feat7

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat7Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat7UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat7FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat7UserSummary
)

data class Feat7UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat7NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat7Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat7Config = Feat7Config()
) {

    fun loadSnapshot(userId: Long): Feat7NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat7NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat7UserSummary {
        return Feat7UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat7FeedItem> {
        val result = ArrayList<Feat7FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat7FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat7UiMapper {

    fun mapToUi(model: List<Feat7FeedItem>): Feat7UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat7UiModel(
            header = UiText("Feat7 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat7UiModel =
        Feat7UiModel(
            header = UiText("No data"),
            items = emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat7UiModel =
        Feat7UiModel(
            header = UiText("Loading..."),
            items = emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat7UiModel =
        Feat7UiModel(
            header = UiText("Error"),
            items = emptyList(),
            loading = false,
            error = message
        )
}

class Feat7Service(
    private val repository: Feat7Repository,
    private val uiMapper: Feat7UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat7UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat7UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat7UserItem1(val user: CoreUser, val label: String)
data class Feat7UserItem2(val user: CoreUser, val label: String)
data class Feat7UserItem3(val user: CoreUser, val label: String)
data class Feat7UserItem4(val user: CoreUser, val label: String)
data class Feat7UserItem5(val user: CoreUser, val label: String)
data class Feat7UserItem6(val user: CoreUser, val label: String)
data class Feat7UserItem7(val user: CoreUser, val label: String)
data class Feat7UserItem8(val user: CoreUser, val label: String)
data class Feat7UserItem9(val user: CoreUser, val label: String)
data class Feat7UserItem10(val user: CoreUser, val label: String)

data class Feat7StateBlock1(val state: Feat7UiModel, val checksum: Int)
data class Feat7StateBlock2(val state: Feat7UiModel, val checksum: Int)
data class Feat7StateBlock3(val state: Feat7UiModel, val checksum: Int)
data class Feat7StateBlock4(val state: Feat7UiModel, val checksum: Int)
data class Feat7StateBlock5(val state: Feat7UiModel, val checksum: Int)
data class Feat7StateBlock6(val state: Feat7UiModel, val checksum: Int)
data class Feat7StateBlock7(val state: Feat7UiModel, val checksum: Int)
data class Feat7StateBlock8(val state: Feat7UiModel, val checksum: Int)
data class Feat7StateBlock9(val state: Feat7UiModel, val checksum: Int)
data class Feat7StateBlock10(val state: Feat7UiModel, val checksum: Int)

fun buildFeat7UserItem(user: CoreUser, index: Int): Feat7UserItem1 {
    return Feat7UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat7StateBlock(model: Feat7UiModel): Feat7StateBlock1 {
    return Feat7StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat7UserSummary> {
    val list = ArrayList<Feat7UserSummary>(users.size)
    for (user in users) {
        list += Feat7UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat7UserSummary>): List<UiListItem> {
    val items = ArrayList<UiListItem>(summaries.size)
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

fun createLargeUiModel(count: Int): Feat7UiModel {
    val summaries = (0 until count).map {
        Feat7UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat7UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = ArrayList<CoreUser>(count)
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
    val list = ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat7UiModel> {
    val models = ArrayList<Feat7UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat7AnalyticsEvent1(val name: String, val value: String)
data class Feat7AnalyticsEvent2(val name: String, val value: String)
data class Feat7AnalyticsEvent3(val name: String, val value: String)
data class Feat7AnalyticsEvent4(val name: String, val value: String)
data class Feat7AnalyticsEvent5(val name: String, val value: String)
data class Feat7AnalyticsEvent6(val name: String, val value: String)
data class Feat7AnalyticsEvent7(val name: String, val value: String)
data class Feat7AnalyticsEvent8(val name: String, val value: String)
data class Feat7AnalyticsEvent9(val name: String, val value: String)
data class Feat7AnalyticsEvent10(val name: String, val value: String)

fun logFeat7Event1(event: Feat7AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat7Event2(event: Feat7AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat7Event3(event: Feat7AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat7Event4(event: Feat7AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat7Event5(event: Feat7AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat7Event6(event: Feat7AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat7Event7(event: Feat7AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat7Event8(event: Feat7AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat7Event9(event: Feat7AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat7Event10(event: Feat7AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat7Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat7Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat7Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat7Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat7Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat7Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat7Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat7Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat7Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat7Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat7(u: CoreUser): Feat7Projection1 =
    Feat7Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat7Projection1> {
    val list = ArrayList<Feat7Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat7(u)
    }
    return list
}
