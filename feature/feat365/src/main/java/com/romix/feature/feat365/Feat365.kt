package com.romix.feature.feat365

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat365Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat365UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat365FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat365UserSummary
)

data class Feat365UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat365NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat365Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat365Config = Feat365Config()
) {

    fun loadSnapshot(userId: Long): Feat365NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat365NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat365UserSummary {
        return Feat365UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat365FeedItem> {
        val result = java.util.ArrayList<Feat365FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat365FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat365UiMapper {

    fun mapToUi(model: List<Feat365FeedItem>): Feat365UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat365UiModel(
            header = UiText("Feat365 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat365UiModel =
        Feat365UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat365UiModel =
        Feat365UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat365UiModel =
        Feat365UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat365Service(
    private val repository: Feat365Repository,
    private val uiMapper: Feat365UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat365UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat365UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat365UserItem1(val user: CoreUser, val label: String)
data class Feat365UserItem2(val user: CoreUser, val label: String)
data class Feat365UserItem3(val user: CoreUser, val label: String)
data class Feat365UserItem4(val user: CoreUser, val label: String)
data class Feat365UserItem5(val user: CoreUser, val label: String)
data class Feat365UserItem6(val user: CoreUser, val label: String)
data class Feat365UserItem7(val user: CoreUser, val label: String)
data class Feat365UserItem8(val user: CoreUser, val label: String)
data class Feat365UserItem9(val user: CoreUser, val label: String)
data class Feat365UserItem10(val user: CoreUser, val label: String)

data class Feat365StateBlock1(val state: Feat365UiModel, val checksum: Int)
data class Feat365StateBlock2(val state: Feat365UiModel, val checksum: Int)
data class Feat365StateBlock3(val state: Feat365UiModel, val checksum: Int)
data class Feat365StateBlock4(val state: Feat365UiModel, val checksum: Int)
data class Feat365StateBlock5(val state: Feat365UiModel, val checksum: Int)
data class Feat365StateBlock6(val state: Feat365UiModel, val checksum: Int)
data class Feat365StateBlock7(val state: Feat365UiModel, val checksum: Int)
data class Feat365StateBlock8(val state: Feat365UiModel, val checksum: Int)
data class Feat365StateBlock9(val state: Feat365UiModel, val checksum: Int)
data class Feat365StateBlock10(val state: Feat365UiModel, val checksum: Int)

fun buildFeat365UserItem(user: CoreUser, index: Int): Feat365UserItem1 {
    return Feat365UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat365StateBlock(model: Feat365UiModel): Feat365StateBlock1 {
    return Feat365StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat365UserSummary> {
    val list = java.util.ArrayList<Feat365UserSummary>(users.size)
    for (user in users) {
        list += Feat365UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat365UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat365UiModel {
    val summaries = (0 until count).map {
        Feat365UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat365UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat365UiModel> {
    val models = java.util.ArrayList<Feat365UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat365AnalyticsEvent1(val name: String, val value: String)
data class Feat365AnalyticsEvent2(val name: String, val value: String)
data class Feat365AnalyticsEvent3(val name: String, val value: String)
data class Feat365AnalyticsEvent4(val name: String, val value: String)
data class Feat365AnalyticsEvent5(val name: String, val value: String)
data class Feat365AnalyticsEvent6(val name: String, val value: String)
data class Feat365AnalyticsEvent7(val name: String, val value: String)
data class Feat365AnalyticsEvent8(val name: String, val value: String)
data class Feat365AnalyticsEvent9(val name: String, val value: String)
data class Feat365AnalyticsEvent10(val name: String, val value: String)

fun logFeat365Event1(event: Feat365AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat365Event2(event: Feat365AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat365Event3(event: Feat365AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat365Event4(event: Feat365AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat365Event5(event: Feat365AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat365Event6(event: Feat365AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat365Event7(event: Feat365AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat365Event8(event: Feat365AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat365Event9(event: Feat365AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat365Event10(event: Feat365AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat365Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat365Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat365Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat365Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat365Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat365Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat365Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat365Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat365Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat365Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat365(u: CoreUser): Feat365Projection1 =
    Feat365Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat365Projection1> {
    val list = java.util.ArrayList<Feat365Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat365(u)
    }
    return list
}
