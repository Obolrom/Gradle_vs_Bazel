package com.romix.feature.feat305

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat305Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat305UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat305FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat305UserSummary
)

data class Feat305UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat305NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat305Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat305Config = Feat305Config()
) {

    fun loadSnapshot(userId: Long): Feat305NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat305NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat305UserSummary {
        return Feat305UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat305FeedItem> {
        val result = java.util.ArrayList<Feat305FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat305FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat305UiMapper {

    fun mapToUi(model: List<Feat305FeedItem>): Feat305UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat305UiModel(
            header = UiText("Feat305 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat305UiModel =
        Feat305UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat305UiModel =
        Feat305UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat305UiModel =
        Feat305UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat305Service(
    private val repository: Feat305Repository,
    private val uiMapper: Feat305UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat305UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat305UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat305UserItem1(val user: CoreUser, val label: String)
data class Feat305UserItem2(val user: CoreUser, val label: String)
data class Feat305UserItem3(val user: CoreUser, val label: String)
data class Feat305UserItem4(val user: CoreUser, val label: String)
data class Feat305UserItem5(val user: CoreUser, val label: String)
data class Feat305UserItem6(val user: CoreUser, val label: String)
data class Feat305UserItem7(val user: CoreUser, val label: String)
data class Feat305UserItem8(val user: CoreUser, val label: String)
data class Feat305UserItem9(val user: CoreUser, val label: String)
data class Feat305UserItem10(val user: CoreUser, val label: String)

data class Feat305StateBlock1(val state: Feat305UiModel, val checksum: Int)
data class Feat305StateBlock2(val state: Feat305UiModel, val checksum: Int)
data class Feat305StateBlock3(val state: Feat305UiModel, val checksum: Int)
data class Feat305StateBlock4(val state: Feat305UiModel, val checksum: Int)
data class Feat305StateBlock5(val state: Feat305UiModel, val checksum: Int)
data class Feat305StateBlock6(val state: Feat305UiModel, val checksum: Int)
data class Feat305StateBlock7(val state: Feat305UiModel, val checksum: Int)
data class Feat305StateBlock8(val state: Feat305UiModel, val checksum: Int)
data class Feat305StateBlock9(val state: Feat305UiModel, val checksum: Int)
data class Feat305StateBlock10(val state: Feat305UiModel, val checksum: Int)

fun buildFeat305UserItem(user: CoreUser, index: Int): Feat305UserItem1 {
    return Feat305UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat305StateBlock(model: Feat305UiModel): Feat305StateBlock1 {
    return Feat305StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat305UserSummary> {
    val list = java.util.ArrayList<Feat305UserSummary>(users.size)
    for (user in users) {
        list += Feat305UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat305UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat305UiModel {
    val summaries = (0 until count).map {
        Feat305UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat305UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat305UiModel> {
    val models = java.util.ArrayList<Feat305UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat305AnalyticsEvent1(val name: String, val value: String)
data class Feat305AnalyticsEvent2(val name: String, val value: String)
data class Feat305AnalyticsEvent3(val name: String, val value: String)
data class Feat305AnalyticsEvent4(val name: String, val value: String)
data class Feat305AnalyticsEvent5(val name: String, val value: String)
data class Feat305AnalyticsEvent6(val name: String, val value: String)
data class Feat305AnalyticsEvent7(val name: String, val value: String)
data class Feat305AnalyticsEvent8(val name: String, val value: String)
data class Feat305AnalyticsEvent9(val name: String, val value: String)
data class Feat305AnalyticsEvent10(val name: String, val value: String)

fun logFeat305Event1(event: Feat305AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat305Event2(event: Feat305AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat305Event3(event: Feat305AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat305Event4(event: Feat305AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat305Event5(event: Feat305AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat305Event6(event: Feat305AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat305Event7(event: Feat305AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat305Event8(event: Feat305AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat305Event9(event: Feat305AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat305Event10(event: Feat305AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat305Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat305Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat305Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat305Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat305Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat305Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat305Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat305Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat305Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat305Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat305(u: CoreUser): Feat305Projection1 =
    Feat305Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat305Projection1> {
    val list = java.util.ArrayList<Feat305Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat305(u)
    }
    return list
}
